import { Event } from "@/features/events";
import { User } from "@/features/users";

interface Appointment {
  id: string;
  event: Event;
  user: User;
}

export { Appointment };
