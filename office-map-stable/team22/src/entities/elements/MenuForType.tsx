import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import type { MarkerTypes } from "@shared/types/marker";

type PositionedMenuProps = {
  open: boolean;
  anchorEl: HTMLElement | null;
  onClose: () => void;
  onSelect: (type: MarkerTypes) => void;
};

export default function PositionedMenu({
  open,
  onClose,
  onSelect,
  anchorEl,
}: PositionedMenuProps) {
  const markerType: MarkerTypes[] = [
    "workspace",
    "room",
    "emergency",
    "utility",
  ];

  return (
    <div>
      <Menu
        id="demo-positioned-menu"
        aria-labelledby="demo-positioned-button"
        anchorEl={anchorEl}
        open={open}
        onClose={onClose}
        anchorOrigin={{
          vertical: "center",
          horizontal: "right",
        }}
        transformOrigin={{
          vertical: "center",
          horizontal: "left",
        }}
      >
        {markerType.map((type) => (
          <MenuItem
            key={type}
            onClick={() => {
              onSelect(type), onClose();
            }}
          >
            {type}
          </MenuItem>
        ))}
      </Menu>
    </div>
  );
}
