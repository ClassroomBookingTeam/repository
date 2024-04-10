import { ComponentProps, ComponentRef, forwardRef } from "react";

import { RecurrenceSelect } from "../components/recurrence-select";
import { useRecurrences } from "../hooks/use-recurrences";

type Props = Omit<ComponentProps<typeof RecurrenceSelect>, "recurrences">;

const RecurrenceSelectContainer = forwardRef<
  ComponentRef<typeof RecurrenceSelect>,
  Props
>((props, ref) => {
  const { data: recurrences = [], isLoading } = useRecurrences();

  if (isLoading) {
    return <RecurrenceSelect recurrences={[]} disabled />;
  }

  return <RecurrenceSelect recurrences={recurrences} {...props} ref={ref} />;
});

RecurrenceSelectContainer.displayName = "RecurrenceSelectContainer";

export { RecurrenceSelectContainer };
