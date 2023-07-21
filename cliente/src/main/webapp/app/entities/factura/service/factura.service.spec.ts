import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFactura } from '../factura.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../factura.test-samples';

import { FacturaService, RestFactura } from './factura.service';

const requireRestSample: RestFactura = {
  ...sampleWithRequiredData,
  fechaPago: sampleWithRequiredData.fechaPago?.toJSON(),
};

describe('Factura Service', () => {
  let service: FacturaService;
  let httpMock: HttpTestingController;
  let expectedResult: IFactura | IFactura[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FacturaService);
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

    it('should create a Factura', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const factura = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(factura).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Factura', () => {
      const factura = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(factura).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Factura', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Factura', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Factura', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addFacturaToCollectionIfMissing', () => {
      it('should add a Factura to an empty array', () => {
        const factura: IFactura = sampleWithRequiredData;
        expectedResult = service.addFacturaToCollectionIfMissing([], factura);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(factura);
      });

      it('should not add a Factura to an array that contains it', () => {
        const factura: IFactura = sampleWithRequiredData;
        const facturaCollection: IFactura[] = [
          {
            ...factura,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFacturaToCollectionIfMissing(facturaCollection, factura);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Factura to an array that doesn't contain it", () => {
        const factura: IFactura = sampleWithRequiredData;
        const facturaCollection: IFactura[] = [sampleWithPartialData];
        expectedResult = service.addFacturaToCollectionIfMissing(facturaCollection, factura);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(factura);
      });

      it('should add only unique Factura to an array', () => {
        const facturaArray: IFactura[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const facturaCollection: IFactura[] = [sampleWithRequiredData];
        expectedResult = service.addFacturaToCollectionIfMissing(facturaCollection, ...facturaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const factura: IFactura = sampleWithRequiredData;
        const factura2: IFactura = sampleWithPartialData;
        expectedResult = service.addFacturaToCollectionIfMissing([], factura, factura2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(factura);
        expect(expectedResult).toContain(factura2);
      });

      it('should accept null and undefined values', () => {
        const factura: IFactura = sampleWithRequiredData;
        expectedResult = service.addFacturaToCollectionIfMissing([], null, factura, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(factura);
      });

      it('should return initial array if no Factura is added', () => {
        const facturaCollection: IFactura[] = [sampleWithRequiredData];
        expectedResult = service.addFacturaToCollectionIfMissing(facturaCollection, undefined, null);
        expect(expectedResult).toEqual(facturaCollection);
      });
    });

    describe('compareFactura', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFactura(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareFactura(entity1, entity2);
        const compareResult2 = service.compareFactura(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareFactura(entity1, entity2);
        const compareResult2 = service.compareFactura(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareFactura(entity1, entity2);
        const compareResult2 = service.compareFactura(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
