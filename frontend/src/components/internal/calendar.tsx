import "react-big-calendar/lib/css/react-big-calendar.css";

import { endOfDay, startOfDay } from "date-fns";
import format from "date-fns/format";
import getDay from "date-fns/getDay";
import ru from "date-fns/locale/ru";
import parse from "date-fns/parse";
import startOfWeek from "date-fns/startOfWeek";
import { useCallback } from "react";
import {
  Calendar as ReactCalendar,
  CalendarProps,
  dateFnsLocalizer,
  Messages,
} from "react-big-calendar";

import { cn } from "@/lib/utils";

import styles from "./calendar.module.css";
import { CalendarToolbar } from "./calendar-toolbar";

const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek,
  getDay,
  locales: { ru },
});

const components = {
  toolbar: CalendarToolbar,
};

const messages: Messages = {
  date: "Дата",
  time: "Время",
  event: "Событие",
  allDay: "Весь день",
  week: "Неделя",
  work_week: "Рабочая неделя",
  day: "День",
  month: "Месяц",
  previous: "Назад",
  next: "Вперёд",
  yesterday: "Вчера",
  tomorrow: "Завтра",
  today: "Сегодня",
  agenda: "Повестка дня",
  noEventsInRange: "События не найдены",
  showMore: (total) => `Ещё ${total}`,
};

interface Props<TEvent extends object = object>
  extends Omit<
    CalendarProps<TEvent>,
    "localizer" | "components" | "onRangeChange"
  > {
  onRangeChange?: (newRange: [Date, Date]) => unknown;
}

const Calendar = <TEvent extends object = object>({
  className,
  style,
  onRangeChange,
  ...props
}: Props<TEvent>) => {
  const onBeforeRangeChange = useCallback<
    NonNullable<CalendarProps["onRangeChange"]>
  >(
    (range) => {
      if (!onRangeChange) {
        return;
      }

      const start = "start" in range ? range.start : range[0];
      const end = "end" in range ? range.end : range[range.length - 1];

      if (start && end) {
        const newRange = [startOfDay(start), endOfDay(end)] as [Date, Date];
        onRangeChange(newRange);
      }
    },
    [onRangeChange],
  );

  return (
    <ReactCalendar
      className={cn(styles.calendar, className)}
      style={{ height: 750, ...style }}
      onRangeChange={onBeforeRangeChange}
      {...props}
      localizer={localizer}
      messages={messages}
      components={components}
    />
  );
};

export { Calendar };
export type { SlotInfo } from "react-big-calendar";
