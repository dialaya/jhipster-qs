import { ICoupon } from 'app/entities/coupon/coupon.model';
import { IMember } from 'app/entities/member/member.model';

export interface IOrder {
  id?: number;
  orderId?: string;
  coupons?: ICoupon[] | null;
  member?: IMember | null;
}

export class Order implements IOrder {
  constructor(public id?: number, public orderId?: string, public coupons?: ICoupon[] | null, public member?: IMember | null) {}
}

export function getOrderIdentifier(order: IOrder): number | undefined {
  return order.id;
}
