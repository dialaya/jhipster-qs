import { ILocation } from 'app/entities/location/location.model';
import { IMembership } from 'app/entities/membership/membership.model';

export interface IOrganization {
  id?: number;
  name?: string;
  description?: string | null;
  baseRate?: number | null;
  groupRate?: number | null;
  fullRate?: number | null;
  location?: ILocation | null;
  memberships?: IMembership[] | null;
}

export class Organization implements IOrganization {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
    public baseRate?: number | null,
    public groupRate?: number | null,
    public fullRate?: number | null,
    public location?: ILocation | null,
    public memberships?: IMembership[] | null
  ) {}
}

export function getOrganizationIdentifier(organization: IOrganization): number | undefined {
  return organization.id;
}
