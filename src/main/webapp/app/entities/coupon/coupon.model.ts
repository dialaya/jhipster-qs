import { IOrder } from 'app/entities/order/order.model';

export interface ICoupon {
  id?: number;
  couponId?: string;
  offRate?: number;
  order?: IOrder | null;
}

export class Coupon implements ICoupon {
  constructor(public id?: number, public couponId?: string, public offRate?: number, public order?: IOrder | null) {}
}

export function getCouponIdentifier(coupon: ICoupon): number | undefined {
  return coupon.id;
}
