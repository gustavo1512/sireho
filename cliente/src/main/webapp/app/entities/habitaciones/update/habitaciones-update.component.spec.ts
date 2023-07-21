import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { HabitacionesFormService } from './habitaciones-form.service';
import { HabitacionesService } from '../service/habitaciones.service';
import { IHabitaciones } from '../habitaciones.model';
import { IFactura } from 'app/entities/factura/factura.model';
import { FacturaService } from 'app/entities/factura/service/factura.service';

import { HabitacionesUpdateComponent } from './habitaciones-update.component';

describe('Habitaciones Management Update Component', () => {
  let comp: HabitacionesUpdateComponent;
  let fixture: ComponentFixture<HabitacionesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let habitacionesFormService: HabitacionesFormService;
  let habitacionesService: HabitacionesService;
  let facturaService: FacturaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), HabitacionesUpdateComponent],
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
      .overrideTemplate(HabitacionesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HabitacionesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    habitacionesFormService = TestBed.inject(HabitacionesFormService);
    habitacionesService = TestBed.inject(HabitacionesService);
    facturaService = TestBed.inject(FacturaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Factura query and add missing value', () => {
      const habitaciones: IHabitaciones = { id: 456 };
      const facturaHabitaciones: IFactura = { id: 61888 };
      habitaciones.facturaHabitaciones = facturaHabitaciones;

      const facturaCollection: IFactura[] = [{ id: 81628 }];
      jest.spyOn(facturaService, 'query').mockReturnValue(of(new HttpResponse({ body: facturaCollection })));
      const additionalFacturas = [facturaHabitaciones];
      const expectedCollection: IFactura[] = [...additionalFacturas, ...facturaCollection];
      jest.spyOn(facturaService, 'addFacturaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ habitaciones });
      comp.ngOnInit();

      expect(facturaService.query).toHaveBeenCalled();
      expect(facturaService.addFacturaToCollectionIfMissing).toHaveBeenCalledWith(
        facturaCollection,
        ...additionalFacturas.map(expect.objectContaining)
      );
      expect(comp.facturasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const habitaciones: IHabitaciones = { id: 456 };
      const facturaHabitaciones: IFactura = { id: 45800 };
      habitaciones.facturaHabitaciones = facturaHabitaciones;

      activatedRoute.data = of({ habitaciones });
      comp.ngOnInit();

      expect(comp.facturasSharedCollection).toContain(facturaHabitaciones);
      expect(comp.habitaciones).toEqual(habitaciones);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHabitaciones>>();
      const habitaciones = { id: 123 };
      jest.spyOn(habitacionesFormService, 'getHabitaciones').mockReturnValue(habitaciones);
      jest.spyOn(habitacionesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ habitaciones });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: habitaciones }));
      saveSubject.complete();

      // THEN
      expect(habitacionesFormService.getHabitaciones).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(habitacionesService.update).toHaveBeenCalledWith(expect.objectContaining(habitaciones));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHabitaciones>>();
      const habitaciones = { id: 123 };
      jest.spyOn(habitacionesFormService, 'getHabitaciones').mockReturnValue({ id: null });
      jest.spyOn(habitacionesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ habitaciones: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: habitaciones }));
      saveSubject.complete();

      // THEN
      expect(habitacionesFormService.getHabitaciones).toHaveBeenCalled();
      expect(habitacionesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHabitaciones>>();
      const habitaciones = { id: 123 };
      jest.spyOn(habitacionesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ habitaciones });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(habitacionesService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareFactura', () => {
      it('Should forward to facturaService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(facturaService, 'compareFactura');
        comp.compareFactura(entity, entity2);
        expect(facturaService.compareFactura).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
