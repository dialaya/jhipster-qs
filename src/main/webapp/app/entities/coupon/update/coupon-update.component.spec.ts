jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CouponService } from '../service/coupon.service';
import { ICoupon, Coupon } from '../coupon.model';
import { IOrder } from 'app/entities/order/order.model';
import { OrderService } from 'app/entities/order/service/order.service';

import { CouponUpdateComponent } from './coupon-update.component';

describe('Component Tests', () => {
  describe('Coupon Management Update Component', () => {
    let comp: CouponUpdateComponent;
    let fixture: ComponentFixture<CouponUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let couponService: CouponService;
    let orderService: OrderService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CouponUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CouponUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CouponUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      couponService = TestBed.inject(CouponService);
      orderService = TestBed.inject(OrderService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Order query and add missing value', () => {
        const coupon: ICoupon = { id: 456 };
        const order: IOrder = { id: 92027 };
        coupon.order = order;

        const orderCollection: IOrder[] = [{ id: 62465 }];
        spyOn(orderService, 'query').and.returnValue(of(new HttpResponse({ body: orderCollection })));
        const additionalOrders = [order];
        const expectedCollection: IOrder[] = [...additionalOrders, ...orderCollection];
        spyOn(orderService, 'addOrderToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ coupon });
        comp.ngOnInit();

        expect(orderService.query).toHaveBeenCalled();
        expect(orderService.addOrderToCollectionIfMissing).toHaveBeenCalledWith(orderCollection, ...additionalOrders);
        expect(comp.ordersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const coupon: ICoupon = { id: 456 };
        const order: IOrder = { id: 40887 };
        coupon.order = order;

        activatedRoute.data = of({ coupon });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(coupon));
        expect(comp.ordersSharedCollection).toContain(order);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const coupon = { id: 123 };
        spyOn(couponService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ coupon });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: coupon }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(couponService.update).toHaveBeenCalledWith(coupon);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const coupon = new Coupon();
        spyOn(couponService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ coupon });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: coupon }));
        saveSubject.complete();

        // THEN
        expect(couponService.create).toHaveBeenCalledWith(coupon);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const coupon = { id: 123 };
        spyOn(couponService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ coupon });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(couponService.update).toHaveBeenCalledWith(coupon);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackOrderById', () => {
        it('Should return tracked Order primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackOrderById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
