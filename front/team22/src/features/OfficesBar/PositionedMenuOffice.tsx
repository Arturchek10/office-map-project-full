import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";

interface PositionedMenuProps {
  menuPos: { x: number; y: number } | null;
  open: boolean;
  onClose: () => void;
  onDelete: () => void;
  onEdit: () => void;
}

const PositionedMenuOffice: React.FC<PositionedMenuProps> = ({
  menuPos,
  open,
  onClose,
  onDelete,
  onEdit,
}) => {
  const handleDelete = () => {
    onDelete();
    onClose();
    onEdit();
  };

  return (
    <Menu
      open={open}
      onClose={onClose}
      anchorReference="anchorPosition"
      anchorPosition={menuPos ? { top: menuPos.y, left: menuPos.x } : undefined}
      sx={{
        zIndex: 9999, // Высокий z-index для отображения поверх всех элементов
      }}
      MenuListProps={{
        sx: {
          minWidth: 120,
          py: 0,
        },
      }}
    >
      <MenuItem
        onClick={onEdit}
        sx={{
          color: "#2F80ED",
          "&:hover": {
            backgroundColor: "#EDF2FA",
          },
        }}
      >
        Редактировать
      </MenuItem>
      <MenuItem
        onClick={handleDelete}
        sx={{
          color: "#d32f2f",
          "&:hover": {
            backgroundColor: "#ffebee",
          },
        }}
      >
        Удалить
      </MenuItem>
    </Menu>
  );
};

export default PositionedMenuOffice;
