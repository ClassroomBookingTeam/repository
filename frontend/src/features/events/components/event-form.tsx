import { FC, ReactNode } from "react";
import { UseFormReturn } from "react-hook-form";

import { DatePicker } from "@/components/internal/date-picker";
import { DateTimePicker } from "@/components/internal/date-time-picker";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { RecurrenceSelect } from "@/features/recurrences";

import { EventFormData } from "../validation/event-form";

interface Props {
  form: UseFormReturn<EventFormData>;
  onSubmit?: (values: EventFormData) => unknown;
  children?: ReactNode;
}

const EventForm: FC<Props> = ({ form, onSubmit, children }) => {
  return (
    <Form {...form}>
      <form
        onSubmit={onSubmit ? form.handleSubmit(onSubmit) : undefined}
        className="space-y-2"
      >
        <FormField
          control={form.control}
          name="title"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Название</FormLabel>
              <FormControl>
                <Input {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="description"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Описание</FormLabel>
              <FormControl>
                <Textarea {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="date_from"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Начало</FormLabel>
              <FormControl>
                <DateTimePicker className="w-full" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="date_to"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Окончание</FormLabel>
              <FormControl>
                <DateTimePicker className="w-full" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="max_appointments"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Максимальное количество мест</FormLabel>
              <FormControl>
                <Input type="number" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="recurrence"
          render={({ field: { onChange, value, ...field } }) => (
            <FormItem>
              <FormLabel>Повторение</FormLabel>
              <FormControl>
                <RecurrenceSelect
                  value={value}
                  onValueChange={onChange}
                  defaultValue={value}
                  {...field}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="recurrence_until"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Повторять до</FormLabel>
              <FormControl>
                <DatePicker className="w-full" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {!!children && (
          <div className="!mt-4 w-full flex gap-2 justify-end">{children}</div>
        )}
      </form>
    </Form>
  );
};

export { EventForm };
