import { ComponentProps, FC, useState } from "react";
import { StringParam, useQueryParam, withDefault } from "use-query-params";

import { DateTimePicker } from "@/components/internal/date-time-picker";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
} from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Building } from "@/features/buildings";
import { RoomPicker } from "@/features/rooms";

interface Props
  extends Omit<ComponentProps<typeof Tabs>, "onSelect">,
    Pick<ComponentProps<typeof RoomPicker>, "onSelect"> {
  buildings: Building[];
}

const LocationPicker: FC<Props> = ({ buildings, onSelect, ...props }) => {
  const [availableAt, setAvailableAt] = useState<Date>();

  const [selectedBuildingId, setSelectedBuildingId] = useQueryParam(
    "event-location-picker:building-id",
    withDefault(StringParam, buildings[0]?.id),
  );

  if (buildings.length === 0) {
    return <p>Нет доступных корпусов</p>;
  }

  return (
    <Tabs
      value={selectedBuildingId}
      onValueChange={setSelectedBuildingId}
      {...props}
    >
      <div className="w-full flex justify-between items-center flex-wrap gap-y-2">
        <TabsList className="flex w-fit">
          {buildings.map((building) => (
            <TabsTrigger value={building.id} key={building.id}>
              {building.name}
            </TabsTrigger>
          ))}
        </TabsList>
        <DateTimePicker value={availableAt} onChange={setAvailableAt} />
      </div>

      {buildings.map((building) => (
        <TabsContent value={building.id} key={building.id}>
          <Card>
            <CardHeader>
              <CardDescription>
                Для создания события выберите аудиторию из списка
              </CardDescription>
            </CardHeader>
            <CardContent>
              <RoomPicker
                buildingId={building.id}
                params={{ available_at: availableAt }}
                onSelect={onSelect}
              />
            </CardContent>
          </Card>
        </TabsContent>
      ))}
    </Tabs>
  );
};

export { LocationPicker };
