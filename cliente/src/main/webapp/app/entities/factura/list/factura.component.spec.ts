import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FacturaService } from '../service/factura.service';

import { FacturaComponent } from './factura.component';

describe('Factura Management Component', () => {
  let comp: FacturaComponent;
  let fixture: ComponentFixture<FacturaComponent>;
  let service: FacturaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'factura', component: FacturaComponent }]),
        HttpClientTestingModule,
        FacturaComponent,
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
      .overrideTemplate(FacturaComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FacturaComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FacturaService);

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
    expect(comp.facturas?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to facturaService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getFacturaIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getFacturaIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
