import { IMeetingRoom } from 'app/shared/model/meeting-room.model';

export interface IEquipment {
  id?: number;
  name?: string;
  description?: string | null;
  isAvailable?: boolean | null;
  meetingRooms?: IMeetingRoom[] | null;
}

export const defaultValue: Readonly<IEquipment> = {
  isAvailable: false,
};
