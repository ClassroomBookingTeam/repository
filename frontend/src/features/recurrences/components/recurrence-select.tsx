import { ComponentProps, forwardRef } from "react";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

import { Recurrence } from "../types/recurrence";

interface Props extends ComponentProps<typeof Select> {
  recurrences: Recurrence[];
  placeholder?: string;
}

const RecurrenceSelect = forwardRef<HTMLSelectElement, Props>(
  ({ recurrences, placeholder, ...props }, ref) => {
    return (
      <Select {...props}>
        <SelectTrigger>
          <SelectValue placeholder={placeholder} ref={ref} />
        </SelectTrigger>

        <SelectContent>
          {recurrences.map((recurrence) => (
            <SelectItem value={recurrence.id} key={recurrence.id}>
              {recurrence.name}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    );
  },
);

RecurrenceSelect.displayName = "RecurrenceSelect";

export { RecurrenceSelect };
