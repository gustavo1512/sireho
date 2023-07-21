import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITipoCargo } from '../tipo-cargo.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../tipo-cargo.test-samples';

import { TipoCargoService } from './tipo-cargo.service';

const requireRestSample: ITipoCargo = {
  ...sampleWithRequiredData,
};

describe('TipoCargo Service', () => {
  let service: TipoCargoService;
  let httpMock: HttpTestingController;
  let expectedResult: ITipoCargo | ITipoCargo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TipoCargoService);
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

    it('should create a TipoCargo', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const tipoCargo = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tipoCargo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TipoCargo', () => {
      const tipoCargo = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tipoCargo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TipoCargo', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TipoCargo', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TipoCargo', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTipoCargoToCollectionIfMissing', () => {
      it('should add a TipoCargo to an empty array', () => {
        const tipoCargo: ITipoCargo = sampleWithRequiredData;
        expectedResult = service.addTipoCargoToCollectionIfMissing([], tipoCargo);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tipoCargo);
      });

      it('should not add a TipoCargo to an array that contains it', () => {
        const tipoCargo: ITipoCargo = sampleWithRequiredData;
        const tipoCargoCollection: ITipoCargo[] = [
          {
            ...tipoCargo,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTipoCargoToCollectionIfMissing(tipoCargoCollection, tipoCargo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TipoCargo to an array that doesn't contain it", () => {
        const tipoCargo: ITipoCargo = sampleWithRequiredData;
        const tipoCargoCollection: ITipoCargo[] = [sampleWithPartialData];
        expectedResult = service.addTipoCargoToCollectionIfMissing(tipoCargoCollection, tipoCargo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tipoCargo);
      });

      it('should add only unique TipoCargo to an array', () => {
        const tipoCargoArray: ITipoCargo[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tipoCargoCollection: ITipoCargo[] = [sampleWithRequiredData];
        expectedResult = service.addTipoCargoToCollectionIfMissing(tipoCargoCollection, ...tipoCargoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tipoCargo: ITipoCargo = sampleWithRequiredData;
        const tipoCargo2: ITipoCargo = sampleWithPartialData;
        expectedResult = service.addTipoCargoToCollectionIfMissing([], tipoCargo, tipoCargo2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tipoCargo);
        expect(expectedResult).toContain(tipoCargo2);
      });

      it('should accept null and undefined values', () => {
        const tipoCargo: ITipoCargo = sampleWithRequiredData;
        expectedResult = service.addTipoCargoToCollectionIfMissing([], null, tipoCargo, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tipoCargo);
      });

      it('should return initial array if no TipoCargo is added', () => {
        const tipoCargoCollection: ITipoCargo[] = [sampleWithRequiredData];
        expectedResult = service.addTipoCargoToCollectionIfMissing(tipoCargoCollection, undefined, null);
        expect(expectedResult).toEqual(tipoCargoCollection);
      });
    });

    describe('compareTipoCargo', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTipoCargo(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTipoCargo(entity1, entity2);
        const compareResult2 = service.compareTipoCargo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTipoCargo(entity1, entity2);
        const compareResult2 = service.compareTipoCargo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTipoCargo(entity1, entity2);
        const compareResult2 = service.compareTipoCargo(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
