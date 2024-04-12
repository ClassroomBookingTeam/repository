import { format } from "date-fns";
import * as _ from "lodash-es";
import { ComponentProps, forwardRef, useEffect, useState } from "react";

import { Input } from "../ui/input";
import { DatePicker } from "./date-picker";

type Props = ComponentProps<typeof DatePicker>;

const DateTimePicker = forwardRef<HTMLInputElement, Props>(
  ({ value, onChange, children, ...props }, ref) => {
    const [time, setTime] = useState("00:00");

    const onBeforeChange: Props["onChange"] = (value) => {
      if (value) {
        value = updateTime(value, time);
      }

      onChange?.(value);
    };

    useEffect(() => {
      if (value) {
        setTime(format(value, "HH:mm"));
      }
    }, [value]);

    useEffect(() => {
      if (value) {
        onBeforeChange(value);
      }
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [time]);

    return (
      <DatePicker
        value={value}
        onChange={onBeforeChange}
        showTime={true}
        {...props}
      >
        <Input
          type="time"
          className="m-3 w-auto mt-0"
          value={time}
          onChange={(e) => setTime(e.currentTarget.value)}
          ref={ref}
        />
        {children}
      </DatePicker>
    );
  },
);

const updateTime = (date: Date, time: string) => {
  const [hours, minutes] = time.split(":").map(Number);
  if (!_.isFinite(hours) || !_.isFinite(minutes)) return date;

  const newDate = new Date(date);

  newDate.setHours(hours);
  newDate.setMinutes(minutes);

  return newDate;
};

DateTimePicker.displayName = "DateTimePicker";

export { DateTimePicker };
