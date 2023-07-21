import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ReservacionesFormService } from './reservaciones-form.service';
import { ReservacionesService } from '../service/reservaciones.service';
import { IReservaciones } from '../reservaciones.model';
import { IHabitaciones } from 'app/entities/habitaciones/habitaciones.model';
import { HabitacionesService } from 'app/entities/habitaciones/service/habitaciones.service';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { ClienteService } from 'app/entities/cliente/service/cliente.service';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';
import { ColaboradorService } from 'app/entities/colaborador/service/colaborador.service';
import { IEventos } from 'app/entities/eventos/eventos.model';
import { EventosService } from 'app/entities/eventos/service/eventos.service';

import { ReservacionesUpdateComponent } from './reservaciones-update.component';

describe('Reservaciones Management Update Component', () => {
  let comp: ReservacionesUpdateComponent;
  let fixture: ComponentFixture<ReservacionesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let reservacionesFormService: ReservacionesFormService;
  let reservacionesService: ReservacionesService;
  let habitacionesService: HabitacionesService;
  let clienteService: ClienteService;
  let colaboradorService: ColaboradorService;
  let eventosService: EventosService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), ReservacionesUpdateComponent],
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
      .overrideTemplate(ReservacionesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReservacionesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    reservacionesFormService = TestBed.inject(ReservacionesFormService);
    reservacionesService = TestBed.inject(ReservacionesService);
    habitacionesService = TestBed.inject(HabitacionesService);
    clienteService = TestBed.inject(ClienteService);
    colaboradorService = TestBed.inject(ColaboradorService);
    eventosService = TestBed.inject(EventosService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call habitacionesReservaciones query and add missing value', () => {
      const reservaciones: IReservaciones = { id: 456 };
      const habitacionesReservaciones: IHabitaciones = { id: 29211 };
      reservaciones.habitacionesReservaciones = habitacionesReservaciones;

      const habitacionesReservacionesCollection: IHabitaciones[] = [{ id: 97123 }];
      jest.spyOn(habitacionesService, 'query').mockReturnValue(of(new HttpResponse({ body: habitacionesReservacionesCollection })));
      const expectedCollection: IHabitaciones[] = [habitacionesReservaciones, ...habitacionesReservacionesCollection];
      jest.spyOn(habitacionesService, 'addHabitacionesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservaciones });
      comp.ngOnInit();

      expect(habitacionesService.query).toHaveBeenCalled();
      expect(habitacionesService.addHabitacionesToCollectionIfMissing).toHaveBeenCalledWith(
        habitacionesReservacionesCollection,
        habitacionesReservaciones
      );
      expect(comp.habitacionesReservacionesCollection).toEqual(expectedCollection);
    });

    it('Should call Cliente query and add missing value', () => {
      const reservaciones: IReservaciones = { id: 456 };
      const clienteReservaciones: ICliente = { id: 89049 };
      reservaciones.clienteReservaciones = clienteReservaciones;

      const clienteCollection: ICliente[] = [{ id: 80950 }];
      jest.spyOn(clienteService, 'query').mockReturnValue(of(new HttpResponse({ body: clienteCollection })));
      const additionalClientes = [clienteReservaciones];
      const expectedCollection: ICliente[] = [...additionalClientes, ...clienteCollection];
      jest.spyOn(clienteService, 'addClienteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservaciones });
      comp.ngOnInit();

      expect(clienteService.query).toHaveBeenCalled();
      expect(clienteService.addClienteToCollectionIfMissing).toHaveBeenCalledWith(
        clienteCollection,
        ...additionalClientes.map(expect.objectContaining)
      );
      expect(comp.clientesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Colaborador query and add missing value', () => {
      const reservaciones: IReservaciones = { id: 456 };
      const colaboradorResrvaciones: IColaborador = { id: 62479 };
      reservaciones.colaboradorResrvaciones = colaboradorResrvaciones;

      const colaboradorCollection: IColaborador[] = [{ id: 97243 }];
      jest.spyOn(colaboradorService, 'query').mockReturnValue(of(new HttpResponse({ body: colaboradorCollection })));
      const additionalColaboradors = [colaboradorResrvaciones];
      const expectedCollection: IColaborador[] = [...additionalColaboradors, ...colaboradorCollection];
      jest.spyOn(colaboradorService, 'addColaboradorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservaciones });
      comp.ngOnInit();

      expect(colaboradorService.query).toHaveBeenCalled();
      expect(colaboradorService.addColaboradorToCollectionIfMissing).toHaveBeenCalledWith(
        colaboradorCollection,
        ...additionalColaboradors.map(expect.objectContaining)
      );
      expect(comp.colaboradorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Eventos query and add missing value', () => {
      const reservaciones: IReservaciones = { id: 456 };
      const eventosReservaciones: IEventos = { id: 60788 };
      reservaciones.eventosReservaciones = eventosReservaciones;

      const eventosCollection: IEventos[] = [{ id: 88634 }];
      jest.spyOn(eventosService, 'query').mockReturnValue(of(new HttpResponse({ body: eventosCollection })));
      const additionalEventos = [eventosReservaciones];
      const expectedCollection: IEventos[] = [...additionalEventos, ...eventosCollection];
      jest.spyOn(eventosService, 'addEventosToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservaciones });
      comp.ngOnInit();

      expect(eventosService.query).toHaveBeenCalled();
      expect(eventosService.addEventosToCollectionIfMissing).toHaveBeenCalledWith(
        eventosCollection,
        ...additionalEventos.map(expect.objectContaining)
      );
      expect(comp.eventosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const reservaciones: IReservaciones = { id: 456 };
      const habitacionesReservaciones: IHabitaciones = { id: 67384 };
      reservaciones.habitacionesReservaciones = habitacionesReservaciones;
      const clienteReservaciones: ICliente = { id: 10410 };
      reservaciones.clienteReservaciones = clienteReservaciones;
      const colaboradorResrvaciones: IColaborador = { id: 23386 };
      reservaciones.colaboradorResrvaciones = colaboradorResrvaciones;
      const eventosReservaciones: IEventos = { id: 28460 };
      reservaciones.eventosReservaciones = eventosReservaciones;

      activatedRoute.data = of({ reservaciones });
      comp.ngOnInit();

      expect(comp.habitacionesReservacionesCollection).toContain(habitacionesReservaciones);
      expect(comp.clientesSharedCollection).toContain(clienteReservaciones);
      expect(comp.colaboradorsSharedCollection).toContain(colaboradorResrvaciones);
      expect(comp.eventosSharedCollection).toContain(eventosReservaciones);
      expect(comp.reservaciones).toEqual(reservaciones);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservaciones>>();
      const reservaciones = { id: 123 };
      jest.spyOn(reservacionesFormService, 'getReservaciones').mockReturnValue(reservaciones);
      jest.spyOn(reservacionesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservaciones });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reservaciones }));
      saveSubject.complete();

      // THEN
      expect(reservacionesFormService.getReservaciones).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(reservacionesService.update).toHaveBeenCalledWith(expect.objectContaining(reservaciones));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservaciones>>();
      const reservaciones = { id: 123 };
      jest.spyOn(reservacionesFormService, 'getReservaciones').mockReturnValue({ id: null });
      jest.spyOn(reservacionesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservaciones: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reservaciones }));
      saveSubject.complete();

      // THEN
      expect(reservacionesFormService.getReservaciones).toHaveBeenCalled();
      expect(reservacionesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservaciones>>();
      const reservaciones = { id: 123 };
      jest.spyOn(reservacionesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservaciones });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(reservacionesService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareHabitaciones', () => {
      it('Should forward to habitacionesService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(habitacionesService, 'compareHabitaciones');
        comp.compareHabitaciones(entity, entity2);
        expect(habitacionesService.compareHabitaciones).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCliente', () => {
      it('Should forward to clienteService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(clienteService, 'compareCliente');
        comp.compareCliente(entity, entity2);
        expect(clienteService.compareCliente).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareColaborador', () => {
      it('Should forward to colaboradorService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(colaboradorService, 'compareColaborador');
        comp.compareColaborador(entity, entity2);
        expect(colaboradorService.compareColaborador).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareEventos', () => {
      it('Should forward to eventosService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(eventosService, 'compareEventos');
        comp.compareEventos(entity, entity2);
        expect(eventosService.compareEventos).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
