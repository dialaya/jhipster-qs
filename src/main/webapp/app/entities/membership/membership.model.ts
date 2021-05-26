import { IOrganization } from 'app/entities/organization/organization.model';
import { IMember } from 'app/entities/member/member.model';

export interface IMembership {
  id?: number;
  memberEmail?: string;
  organisationName?: string;
  organization?: IOrganization | null;
  member?: IMember | null;
}

export class Membership implements IMembership {
  constructor(
    public id?: number,
    public memberEmail?: string,
    public organisationName?: string,
    public organization?: IOrganization | null,
    public member?: IMember | null
  ) {}
}

export function getMembershipIdentifier(membership: IMembership): number | undefined {
  return membership.id;
}
