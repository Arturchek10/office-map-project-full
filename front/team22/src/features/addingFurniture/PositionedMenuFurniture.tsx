import React from "react"
import { Menu, MenuItem } from "@mui/material"

interface PositionedMenuProps {
  menuPos: { x: number; y: number } | null
  open: boolean
  onClose: () => void
  onDelete: () => void
}

const PositionedMenuFurniture: React.FC<PositionedMenuProps> = ({
  menuPos,
  open,
  onClose,
  onDelete,
}) => {
  const handleDelete = () => {
    onDelete()
    onClose()
  }

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
  )
}

export default PositionedMenuFurniture
