import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReservacionesService } from '../service/reservaciones.service';

import { ReservacionesComponent } from './reservaciones.component';

describe('Reservaciones Management Component', () => {
  let comp: ReservacionesComponent;
  let fixture: ComponentFixture<ReservacionesComponent>;
  let service: ReservacionesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'reservaciones', component: ReservacionesComponent }]),
        HttpClientTestingModule,
        ReservacionesComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ReservacionesComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReservacionesComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ReservacionesService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.reservaciones?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to reservacionesService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getReservacionesIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getReservacionesIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
