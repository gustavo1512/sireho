import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEventos } from '../eventos.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../eventos.test-samples';

import { EventosService, RestEventos } from './eventos.service';

const requireRestSample: RestEventos = {
  ...sampleWithRequiredData,
  fechaHora: sampleWithRequiredData.fechaHora?.toJSON(),
};

describe('Eventos Service', () => {
  let service: EventosService;
  let httpMock: HttpTestingController;
  let expectedResult: IEventos | IEventos[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EventosService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Eventos', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const eventos = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(eventos).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Eventos', () => {
      const eventos = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(eventos).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Eventos', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Eventos', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Eventos', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addEventosToCollectionIfMissing', () => {
      it('should add a Eventos to an empty array', () => {
        const eventos: IEventos = sampleWithRequiredData;
        expectedResult = service.addEventosToCollectionIfMissing([], eventos);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(eventos);
      });

      it('should not add a Eventos to an array that contains it', () => {
        const eventos: IEventos = sampleWithRequiredData;
        const eventosCollection: IEventos[] = [
          {
            ...eventos,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEventosToCollectionIfMissing(eventosCollection, eventos);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Eventos to an array that doesn't contain it", () => {
        const eventos: IEventos = sampleWithRequiredData;
        const eventosCollection: IEventos[] = [sampleWithPartialData];
        expectedResult = service.addEventosToCollectionIfMissing(eventosCollection, eventos);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(eventos);
      });

      it('should add only unique Eventos to an array', () => {
        const eventosArray: IEventos[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const eventosCollection: IEventos[] = [sampleWithRequiredData];
        expectedResult = service.addEventosToCollectionIfMissing(eventosCollection, ...eventosArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const eventos: IEventos = sampleWithRequiredData;
        const eventos2: IEventos = sampleWithPartialData;
        expectedResult = service.addEventosToCollectionIfMissing([], eventos, eventos2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(eventos);
        expect(expectedResult).toContain(eventos2);
      });

      it('should accept null and undefined values', () => {
        const eventos: IEventos = sampleWithRequiredData;
        expectedResult = service.addEventosToCollectionIfMissing([], null, eventos, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(eventos);
      });

      it('should return initial array if no Eventos is added', () => {
        const eventosCollection: IEventos[] = [sampleWithRequiredData];
        expectedResult = service.addEventosToCollectionIfMissing(eventosCollection, undefined, null);
        expect(expectedResult).toEqual(eventosCollection);
      });
    });

    describe('compareEventos', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEventos(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEventos(entity1, entity2);
        const compareResult2 = service.compareEventos(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEventos(entity1, entity2);
        const compareResult2 = service.compareEventos(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEventos(entity1, entity2);
        const compareResult2 = service.compareEventos(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
