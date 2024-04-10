import { Recurrence } from "@/features/recurrences";
import { Room } from "@/features/rooms";
import { User } from "@/features/users";

interface Event {
  id: string;
  user: User;
  room: Room;
  title: string | null;
  description: string | null;
  recurrence: Recurrence | null;
  recurrence_until: Date | null;
  date_from: Date;
  date_to: Date;
  max_appointments: number | null;
  remaining_appointments: number | null;
  appointments_count: number;
  appointment_id: string | null;
}

export { Event };
