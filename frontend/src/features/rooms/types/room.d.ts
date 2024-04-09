import { Building } from "@/features/buildings";

interface Room {
  id: string;
  number: string;
  floor: number;
  building: Building;
}

export { Room };
