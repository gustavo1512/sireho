import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IReservaciones } from '../reservaciones.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../reservaciones.test-samples';

import { ReservacionesService, RestReservaciones } from './reservaciones.service';

const requireRestSample: RestReservaciones = {
  ...sampleWithRequiredData,
  fechaInicio: sampleWithRequiredData.fechaInicio?.toJSON(),
  fechaFinal: sampleWithRequiredData.fechaFinal?.toJSON(),
};

describe('Reservaciones Service', () => {
  let service: ReservacionesService;
  let httpMock: HttpTestingController;
  let expectedResult: IReservaciones | IReservaciones[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ReservacionesService);
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

    it('should create a Reservaciones', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const reservaciones = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(reservaciones).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Reservaciones', () => {
      const reservaciones = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(reservaciones).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Reservaciones', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Reservaciones', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Reservaciones', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addReservacionesToCollectionIfMissing', () => {
      it('should add a Reservaciones to an empty array', () => {
        const reservaciones: IReservaciones = sampleWithRequiredData;
        expectedResult = service.addReservacionesToCollectionIfMissing([], reservaciones);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(reservaciones);
      });

      it('should not add a Reservaciones to an array that contains it', () => {
        const reservaciones: IReservaciones = sampleWithRequiredData;
        const reservacionesCollection: IReservaciones[] = [
          {
            ...reservaciones,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addReservacionesToCollectionIfMissing(reservacionesCollection, reservaciones);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Reservaciones to an array that doesn't contain it", () => {
        const reservaciones: IReservaciones = sampleWithRequiredData;
        const reservacionesCollection: IReservaciones[] = [sampleWithPartialData];
        expectedResult = service.addReservacionesToCollectionIfMissing(reservacionesCollection, reservaciones);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(reservaciones);
      });

      it('should add only unique Reservaciones to an array', () => {
        const reservacionesArray: IReservaciones[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const reservacionesCollection: IReservaciones[] = [sampleWithRequiredData];
        expectedResult = service.addReservacionesToCollectionIfMissing(reservacionesCollection, ...reservacionesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const reservaciones: IReservaciones = sampleWithRequiredData;
        const reservaciones2: IReservaciones = sampleWithPartialData;
        expectedResult = service.addReservacionesToCollectionIfMissing([], reservaciones, reservaciones2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(reservaciones);
        expect(expectedResult).toContain(reservaciones2);
      });

      it('should accept null and undefined values', () => {
        const reservaciones: IReservaciones = sampleWithRequiredData;
        expectedResult = service.addReservacionesToCollectionIfMissing([], null, reservaciones, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(reservaciones);
      });

      it('should return initial array if no Reservaciones is added', () => {
        const reservacionesCollection: IReservaciones[] = [sampleWithRequiredData];
        expectedResult = service.addReservacionesToCollectionIfMissing(reservacionesCollection, undefined, null);
        expect(expectedResult).toEqual(reservacionesCollection);
      });
    });

    describe('compareReservaciones', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareReservaciones(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareReservaciones(entity1, entity2);
        const compareResult2 = service.compareReservaciones(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareReservaciones(entity1, entity2);
        const compareResult2 = service.compareReservaciones(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareReservaciones(entity1, entity2);
        const compareResult2 = service.compareReservaciones(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
