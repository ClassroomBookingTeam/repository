import { ComponentProps, forwardRef } from "react";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

import { Building } from "../types/building";

interface Props extends ComponentProps<typeof Select> {
  buildings: Building[];
  placeholder?: string;
}

const BuildingSelect = forwardRef<HTMLSelectElement, Props>(
  ({ buildings, placeholder, ...props }, ref) => {
    return (
      <Select {...props}>
        <SelectTrigger>
          <SelectValue placeholder={placeholder} ref={ref} />
        </SelectTrigger>

        <SelectContent>
          {buildings.map((building) => (
            <SelectItem value={building.id} key={building.id}>
              {building.name}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    );
  },
);

BuildingSelect.displayName = "BuildingSelect";

export { BuildingSelect };
