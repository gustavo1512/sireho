import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ColaboradorService } from '../service/colaborador.service';

import { ColaboradorComponent } from './colaborador.component';

describe('Colaborador Management Component', () => {
  let comp: ColaboradorComponent;
  let fixture: ComponentFixture<ColaboradorComponent>;
  let service: ColaboradorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'colaborador', component: ColaboradorComponent }]),
        HttpClientTestingModule,
        ColaboradorComponent,
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
      .overrideTemplate(ColaboradorComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ColaboradorComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ColaboradorService);

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
    expect(comp.colaboradors?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to colaboradorService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getColaboradorIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getColaboradorIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
