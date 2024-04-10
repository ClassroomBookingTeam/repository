import { isBefore } from "date-fns";
import * as _ from "lodash-es";
import { FC } from "react";

import { Button, ButtonProps } from "@/components/ui/button";
import { Event } from "@/features/events";

interface Props extends ButtonProps {
  event: Event;
}

const CreateAppointmentButton: FC<Props> = ({ event, ...props }) => {
  if (isBefore(event.date_from, new Date())) {
    return <Button disabled>Запись закрыта</Button>;
  }

  if (event.remaining_appointments === 0) {
    return <Button disabled>Нет мест</Button>;
  }

  return (
    <div className="flex items-center gap-4">
      <Button {...props}>Записаться</Button>

      {_.isNumber(event.remaining_appointments) &&
        _.isNumber(event.max_appointments) && (
          <p>
            Свободных мест: {event.remaining_appointments}/
            {event.max_appointments}
          </p>
        )}
    </div>
  );
};

export { CreateAppointmentButton };
