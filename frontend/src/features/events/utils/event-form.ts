import { Event } from "../types/event";
import { EventFormData } from "../validation/event-form";

const event2form = (event: Event): EventFormData => ({
  title: event.title ?? undefined,
  description: event.description ?? undefined,
  date_from: event.date_from ?? undefined,
  date_to: event.date_to ?? undefined,
  recurrence: event.recurrence?.id ?? undefined,
  recurrence_until: event.recurrence_until ?? undefined,
  max_appointments: event.max_appointments ?? undefined,
});

export { event2form };
