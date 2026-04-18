import {
  ListItemButton,
  ListItemAvatar,
  Avatar,
  ListItemText,
} from "@mui/material";

import type { TFurniture } from "./type/firniture";

interface FurnitureProps extends TFurniture {
  onClick?: () => void;
}

function Furniture({ name, photoUrl, onClick }: FurnitureProps) {
  return (
    <ListItemButton sx={{border: "1px solid #2F80ED", borderRadius: "5px", mb: "5px"}} onClick={onClick}>
      <ListItemAvatar>
        <Avatar
          src={photoUrl}
          alt={name}
          sx={{ width: 40, height: 40 }}
          variant="square"
        />
      </ListItemAvatar>
      <ListItemText primary={name} />
    </ListItemButton>
  );
}

export default Furniture;