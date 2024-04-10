import { FC, useEffect } from "react";
import { ToolbarProps, View } from "react-big-calendar";

import { Button } from "../ui/button";
import { Tabs, TabsList, TabsTrigger } from "../ui/tabs";

const CalendarToolbar: FC<ToolbarProps> = ({
  label,
  view,
  onNavigate,
  onView,
}) => {
  useEffect(() => {
    // Вызывает onRangeChange при первом рендере
    onView(view);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div className="flex justify-between items-center mb-2 gap-2 flex-wrap">
      <div className="flex gap-1">
        <Button variant="outline" onClick={() => onNavigate("PREV")}>
          Назад
        </Button>
        <Button variant="outline" onClick={() => onNavigate("TODAY")}>
          Сегодня
        </Button>
        <Button variant="outline" onClick={() => onNavigate("NEXT")}>
          Вперед
        </Button>
      </div>

      <p className="font-medium">{label}</p>

      <div className="flex gap-1">
        <Tabs value={view} onValueChange={(view) => onView(view as View)}>
          <TabsList className="flex w-fit">
            <TabsTrigger value="week">Неделя</TabsTrigger>
            <TabsTrigger value="day">День</TabsTrigger>
            <TabsTrigger value="agenda">План</TabsTrigger>
          </TabsList>
        </Tabs>
      </div>
    </div>
  );
};

export { CalendarToolbar };
