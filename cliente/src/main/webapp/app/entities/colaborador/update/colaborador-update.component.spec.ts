import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ColaboradorFormService } from './colaborador-form.service';
import { ColaboradorService } from '../service/colaborador.service';
import { IColaborador } from '../colaborador.model';
import { ITipoCargo } from 'app/entities/tipo-cargo/tipo-cargo.model';
import { TipoCargoService } from 'app/entities/tipo-cargo/service/tipo-cargo.service';

import { ColaboradorUpdateComponent } from './colaborador-update.component';

describe('Colaborador Management Update Component', () => {
  let comp: ColaboradorUpdateComponent;
  let fixture: ComponentFixture<ColaboradorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let colaboradorFormService: ColaboradorFormService;
  let colaboradorService: ColaboradorService;
  let tipoCargoService: TipoCargoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), ColaboradorUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ColaboradorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ColaboradorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    colaboradorFormService = TestBed.inject(ColaboradorFormService);
    colaboradorService = TestBed.inject(ColaboradorService);
    tipoCargoService = TestBed.inject(TipoCargoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TipoCargo query and add missing value', () => {
      const colaborador: IColaborador = { id: 456 };
      const tipoCargoColaborador: ITipoCargo = { id: 13717 };
      colaborador.tipoCargoColaborador = tipoCargoColaborador;

      const tipoCargoCollection: ITipoCargo[] = [{ id: 46302 }];
      jest.spyOn(tipoCargoService, 'query').mockReturnValue(of(new HttpResponse({ body: tipoCargoCollection })));
      const additionalTipoCargos = [tipoCargoColaborador];
      const expectedCollection: ITipoCargo[] = [...additionalTipoCargos, ...tipoCargoCollection];
      jest.spyOn(tipoCargoService, 'addTipoCargoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ colaborador });
      comp.ngOnInit();

      expect(tipoCargoService.query).toHaveBeenCalled();
      expect(tipoCargoService.addTipoCargoToCollectionIfMissing).toHaveBeenCalledWith(
        tipoCargoCollection,
        ...additionalTipoCargos.map(expect.objectContaining)
      );
      expect(comp.tipoCargosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const colaborador: IColaborador = { id: 456 };
      const tipoCargoColaborador: ITipoCargo = { id: 85513 };
      colaborador.tipoCargoColaborador = tipoCargoColaborador;

      activatedRoute.data = of({ colaborador });
      comp.ngOnInit();

      expect(comp.tipoCargosSharedCollection).toContain(tipoCargoColaborador);
      expect(comp.colaborador).toEqual(colaborador);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IColaborador>>();
      const colaborador = { id: 123 };
      jest.spyOn(colaboradorFormService, 'getColaborador').mockReturnValue(colaborador);
      jest.spyOn(colaboradorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ colaborador });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: colaborador }));
      saveSubject.complete();

      // THEN
      expect(colaboradorFormService.getColaborador).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(colaboradorService.update).toHaveBeenCalledWith(expect.objectContaining(colaborador));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IColaborador>>();
      const colaborador = { id: 123 };
      jest.spyOn(colaboradorFormService, 'getColaborador').mockReturnValue({ id: null });
      jest.spyOn(colaboradorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ colaborador: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: colaborador }));
      saveSubject.complete();

      // THEN
      expect(colaboradorFormService.getColaborador).toHaveBeenCalled();
      expect(colaboradorService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IColaborador>>();
      const colaborador = { id: 123 };
      jest.spyOn(colaboradorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ colaborador });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(colaboradorService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTipoCargo', () => {
      it('Should forward to tipoCargoService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(tipoCargoService, 'compareTipoCargo');
        comp.compareTipoCargo(entity, entity2);
        expect(tipoCargoService.compareTipoCargo).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
