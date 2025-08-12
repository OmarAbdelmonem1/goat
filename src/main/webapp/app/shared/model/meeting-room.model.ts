import { IEquipment } from 'app/shared/model/equipment.model';

export interface IMeetingRoom {
  id?: number;
  name?: string;
  capacity?: number;
  requiresApproval?: boolean;
  equipment?: IEquipment[] | null;
}

export const defaultValue: Readonly<IMeetingRoom> = {
  requiresApproval: false,
};
