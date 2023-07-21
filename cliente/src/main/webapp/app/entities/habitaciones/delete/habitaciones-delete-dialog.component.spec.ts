jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { HabitacionesService } from '../service/habitaciones.service';

import { HabitacionesDeleteDialogComponent } from './habitaciones-delete-dialog.component';

describe('Habitaciones Management Delete Component', () => {
  let comp: HabitacionesDeleteDialogComponent;
  let fixture: ComponentFixture<HabitacionesDeleteDialogComponent>;
  let service: HabitacionesService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, HabitacionesDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(HabitacionesDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(HabitacionesDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(HabitacionesService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
