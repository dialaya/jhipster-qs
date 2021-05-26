import * as dayjs from 'dayjs';
import { ILocation } from 'app/entities/location/location.model';
import { IOrder } from 'app/entities/order/order.model';
import { IMembership } from 'app/entities/membership/membership.model';

export interface IMember {
  id?: number;
  title?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  email?: string;
  phoneNumber?: string;
  requestDate?: dayjs.Dayjs | null;
  location?: ILocation | null;
  orders?: IOrder[] | null;
  memberships?: IMembership[] | null;
  legalRepresentative?: IMember | null;
}

export class Member implements IMember {
  constructor(
    public id?: number,
    public title?: string | null,
    public firstName?: string | null,
    public lastName?: string | null,
    public email?: string,
    public phoneNumber?: string,
    public requestDate?: dayjs.Dayjs | null,
    public location?: ILocation | null,
    public orders?: IOrder[] | null,
    public memberships?: IMembership[] | null,
    public legalRepresentative?: IMember | null
  ) {}
}

export function getMemberIdentifier(member: IMember): number | undefined {
  return member.id;
}
