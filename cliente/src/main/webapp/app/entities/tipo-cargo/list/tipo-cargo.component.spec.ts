import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TipoCargoService } from '../service/tipo-cargo.service';

import { TipoCargoComponent } from './tipo-cargo.component';

describe('TipoCargo Management Component', () => {
  let comp: TipoCargoComponent;
  let fixture: ComponentFixture<TipoCargoComponent>;
  let service: TipoCargoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'tipo-cargo', component: TipoCargoComponent }]),
        HttpClientTestingModule,
        TipoCargoComponent,
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
      .overrideTemplate(TipoCargoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TipoCargoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TipoCargoService);

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
    expect(comp.tipoCargos?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to tipoCargoService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTipoCargoIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTipoCargoIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
