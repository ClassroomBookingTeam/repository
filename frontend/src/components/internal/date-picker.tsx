import { format } from "date-fns";
import { ru } from "date-fns/locale";
import { Calendar as CalendarIcon } from "lucide-react";
import { forwardRef, ReactNode } from "react";

import { cn } from "@/lib/utils";

import { Button } from "../ui/button";
import { Calendar } from "../ui/calendar";
import { Popover, PopoverContent, PopoverTrigger } from "../ui/popover";

interface Props {
  value?: Date;
  onChange?: (value?: Date) => unknown;
  children?: ReactNode;
  showTime?: boolean;
  className?: string;
}

const DatePicker = forwardRef<HTMLButtonElement, Props>(
  ({ value, onChange, children, showTime = false, className }, ref) => {
    return (
      <Popover>
        <PopoverTrigger asChild>
          <Button
            variant={"outline"}
            className={cn(
              "justify-start text-left font-normal",
              !value && "text-muted-foreground",
              className,
            )}
            ref={ref}
          >
            <CalendarIcon className="mr-2 h-4 w-4" />
            {value ? (
              format(value, showTime ? "PPP HH:mm" : "PPP")
            ) : (
              <span>Выберите дату</span>
            )}
          </Button>
        </PopoverTrigger>

        <PopoverContent className="w-auto p-0 grid">
          <Calendar
            mode="single"
            selected={value}
            onSelect={onChange}
            initialFocus
            locale={ru}
          />
          {children}
        </PopoverContent>
      </Popover>
    );
  },
);

DatePicker.displayName = "DatePicker";

export { DatePicker };
