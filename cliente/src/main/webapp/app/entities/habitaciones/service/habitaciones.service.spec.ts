import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IHabitaciones } from '../habitaciones.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../habitaciones.test-samples';

import { HabitacionesService } from './habitaciones.service';

const requireRestSample: IHabitaciones = {
  ...sampleWithRequiredData,
};

describe('Habitaciones Service', () => {
  let service: HabitacionesService;
  let httpMock: HttpTestingController;
  let expectedResult: IHabitaciones | IHabitaciones[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(HabitacionesService);
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

    it('should create a Habitaciones', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const habitaciones = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(habitaciones).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Habitaciones', () => {
      const habitaciones = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(habitaciones).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Habitaciones', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Habitaciones', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Habitaciones', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addHabitacionesToCollectionIfMissing', () => {
      it('should add a Habitaciones to an empty array', () => {
        const habitaciones: IHabitaciones = sampleWithRequiredData;
        expectedResult = service.addHabitacionesToCollectionIfMissing([], habitaciones);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(habitaciones);
      });

      it('should not add a Habitaciones to an array that contains it', () => {
        const habitaciones: IHabitaciones = sampleWithRequiredData;
        const habitacionesCollection: IHabitaciones[] = [
          {
            ...habitaciones,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addHabitacionesToCollectionIfMissing(habitacionesCollection, habitaciones);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Habitaciones to an array that doesn't contain it", () => {
        const habitaciones: IHabitaciones = sampleWithRequiredData;
        const habitacionesCollection: IHabitaciones[] = [sampleWithPartialData];
        expectedResult = service.addHabitacionesToCollectionIfMissing(habitacionesCollection, habitaciones);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(habitaciones);
      });

      it('should add only unique Habitaciones to an array', () => {
        const habitacionesArray: IHabitaciones[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const habitacionesCollection: IHabitaciones[] = [sampleWithRequiredData];
        expectedResult = service.addHabitacionesToCollectionIfMissing(habitacionesCollection, ...habitacionesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const habitaciones: IHabitaciones = sampleWithRequiredData;
        const habitaciones2: IHabitaciones = sampleWithPartialData;
        expectedResult = service.addHabitacionesToCollectionIfMissing([], habitaciones, habitaciones2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(habitaciones);
        expect(expectedResult).toContain(habitaciones2);
      });

      it('should accept null and undefined values', () => {
        const habitaciones: IHabitaciones = sampleWithRequiredData;
        expectedResult = service.addHabitacionesToCollectionIfMissing([], null, habitaciones, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(habitaciones);
      });

      it('should return initial array if no Habitaciones is added', () => {
        const habitacionesCollection: IHabitaciones[] = [sampleWithRequiredData];
        expectedResult = service.addHabitacionesToCollectionIfMissing(habitacionesCollection, undefined, null);
        expect(expectedResult).toEqual(habitacionesCollection);
      });
    });

    describe('compareHabitaciones', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareHabitaciones(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareHabitaciones(entity1, entity2);
        const compareResult2 = service.compareHabitaciones(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareHabitaciones(entity1, entity2);
        const compareResult2 = service.compareHabitaciones(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareHabitaciones(entity1, entity2);
        const compareResult2 = service.compareHabitaciones(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
