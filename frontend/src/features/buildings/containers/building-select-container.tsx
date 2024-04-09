import { ComponentProps, ComponentRef, forwardRef } from "react";

import { BuildingSelect } from "../components/building-select";
import { useBuildings } from "../hooks/use-buildings";

type Props = Omit<ComponentProps<typeof BuildingSelect>, "buildings">;

const BuildingSelectContainer = forwardRef<
  ComponentRef<typeof BuildingSelect>,
  Props
>((props, ref) => {
  const { data: buildings = [] } = useBuildings();

  return <BuildingSelect buildings={buildings} {...props} ref={ref} />;
});

BuildingSelectContainer.displayName = "BuildingSelectContainer";

export { BuildingSelectContainer };
