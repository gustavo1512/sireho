import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EventosFormService } from './eventos-form.service';
import { EventosService } from '../service/eventos.service';
import { IEventos } from '../eventos.model';

import { EventosUpdateComponent } from './eventos-update.component';

describe('Eventos Management Update Component', () => {
  let comp: EventosUpdateComponent;
  let fixture: ComponentFixture<EventosUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let eventosFormService: EventosFormService;
  let eventosService: EventosService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), EventosUpdateComponent],
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
      .overrideTemplate(EventosUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EventosUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    eventosFormService = TestBed.inject(EventosFormService);
    eventosService = TestBed.inject(EventosService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const eventos: IEventos = { id: 456 };

      activatedRoute.data = of({ eventos });
      comp.ngOnInit();

      expect(comp.eventos).toEqual(eventos);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEventos>>();
      const eventos = { id: 123 };
      jest.spyOn(eventosFormService, 'getEventos').mockReturnValue(eventos);
      jest.spyOn(eventosService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eventos });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: eventos }));
      saveSubject.complete();

      // THEN
      expect(eventosFormService.getEventos).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(eventosService.update).toHaveBeenCalledWith(expect.objectContaining(eventos));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEventos>>();
      const eventos = { id: 123 };
      jest.spyOn(eventosFormService, 'getEventos').mockReturnValue({ id: null });
      jest.spyOn(eventosService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eventos: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: eventos }));
      saveSubject.complete();

      // THEN
      expect(eventosFormService.getEventos).toHaveBeenCalled();
      expect(eventosService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEventos>>();
      const eventos = { id: 123 };
      jest.spyOn(eventosService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eventos });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(eventosService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
