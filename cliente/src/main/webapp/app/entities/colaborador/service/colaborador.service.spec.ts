import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IColaborador } from '../colaborador.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../colaborador.test-samples';

import { ColaboradorService } from './colaborador.service';

const requireRestSample: IColaborador = {
  ...sampleWithRequiredData,
};

describe('Colaborador Service', () => {
  let service: ColaboradorService;
  let httpMock: HttpTestingController;
  let expectedResult: IColaborador | IColaborador[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ColaboradorService);
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

    it('should create a Colaborador', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const colaborador = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(colaborador).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Colaborador', () => {
      const colaborador = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(colaborador).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Colaborador', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Colaborador', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Colaborador', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addColaboradorToCollectionIfMissing', () => {
      it('should add a Colaborador to an empty array', () => {
        const colaborador: IColaborador = sampleWithRequiredData;
        expectedResult = service.addColaboradorToCollectionIfMissing([], colaborador);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(colaborador);
      });

      it('should not add a Colaborador to an array that contains it', () => {
        const colaborador: IColaborador = sampleWithRequiredData;
        const colaboradorCollection: IColaborador[] = [
          {
            ...colaborador,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addColaboradorToCollectionIfMissing(colaboradorCollection, colaborador);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Colaborador to an array that doesn't contain it", () => {
        const colaborador: IColaborador = sampleWithRequiredData;
        const colaboradorCollection: IColaborador[] = [sampleWithPartialData];
        expectedResult = service.addColaboradorToCollectionIfMissing(colaboradorCollection, colaborador);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(colaborador);
      });

      it('should add only unique Colaborador to an array', () => {
        const colaboradorArray: IColaborador[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const colaboradorCollection: IColaborador[] = [sampleWithRequiredData];
        expectedResult = service.addColaboradorToCollectionIfMissing(colaboradorCollection, ...colaboradorArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const colaborador: IColaborador = sampleWithRequiredData;
        const colaborador2: IColaborador = sampleWithPartialData;
        expectedResult = service.addColaboradorToCollectionIfMissing([], colaborador, colaborador2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(colaborador);
        expect(expectedResult).toContain(colaborador2);
      });

      it('should accept null and undefined values', () => {
        const colaborador: IColaborador = sampleWithRequiredData;
        expectedResult = service.addColaboradorToCollectionIfMissing([], null, colaborador, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(colaborador);
      });

      it('should return initial array if no Colaborador is added', () => {
        const colaboradorCollection: IColaborador[] = [sampleWithRequiredData];
        expectedResult = service.addColaboradorToCollectionIfMissing(colaboradorCollection, undefined, null);
        expect(expectedResult).toEqual(colaboradorCollection);
      });
    });

    describe('compareColaborador', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareColaborador(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareColaborador(entity1, entity2);
        const compareResult2 = service.compareColaborador(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareColaborador(entity1, entity2);
        const compareResult2 = service.compareColaborador(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareColaborador(entity1, entity2);
        const compareResult2 = service.compareColaborador(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
