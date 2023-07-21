import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TipoCargoFormService } from './tipo-cargo-form.service';
import { TipoCargoService } from '../service/tipo-cargo.service';
import { ITipoCargo } from '../tipo-cargo.model';

import { TipoCargoUpdateComponent } from './tipo-cargo-update.component';

describe('TipoCargo Management Update Component', () => {
  let comp: TipoCargoUpdateComponent;
  let fixture: ComponentFixture<TipoCargoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tipoCargoFormService: TipoCargoFormService;
  let tipoCargoService: TipoCargoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), TipoCargoUpdateComponent],
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
      .overrideTemplate(TipoCargoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TipoCargoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tipoCargoFormService = TestBed.inject(TipoCargoFormService);
    tipoCargoService = TestBed.inject(TipoCargoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const tipoCargo: ITipoCargo = { id: 456 };

      activatedRoute.data = of({ tipoCargo });
      comp.ngOnInit();

      expect(comp.tipoCargo).toEqual(tipoCargo);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITipoCargo>>();
      const tipoCargo = { id: 123 };
      jest.spyOn(tipoCargoFormService, 'getTipoCargo').mockReturnValue(tipoCargo);
      jest.spyOn(tipoCargoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tipoCargo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tipoCargo }));
      saveSubject.complete();

      // THEN
      expect(tipoCargoFormService.getTipoCargo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tipoCargoService.update).toHaveBeenCalledWith(expect.objectContaining(tipoCargo));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITipoCargo>>();
      const tipoCargo = { id: 123 };
      jest.spyOn(tipoCargoFormService, 'getTipoCargo').mockReturnValue({ id: null });
      jest.spyOn(tipoCargoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tipoCargo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tipoCargo }));
      saveSubject.complete();

      // THEN
      expect(tipoCargoFormService.getTipoCargo).toHaveBeenCalled();
      expect(tipoCargoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITipoCargo>>();
      const tipoCargo = { id: 123 };
      jest.spyOn(tipoCargoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tipoCargo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tipoCargoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
