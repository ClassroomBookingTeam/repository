import { ComponentProps, FC } from "react";

import { useBuildings } from "@/features/buildings";

import { LocationPicker } from "../components/location-picker";

type Props = Omit<ComponentProps<typeof LocationPicker>, "buildings">;

const LocationPickerContainer: FC<Props> = (props) => {
  const { data: buildings = [], isLoading, isError } = useBuildings();

  if (isLoading) {
    return <p>Загрузка списка корпусов...</p>;
  }

  if (isError) {
    return <p>Не удалось загрузить список корпусов</p>;
  }

  return <LocationPicker {...props} buildings={buildings} />;
};

export { LocationPickerContainer };
