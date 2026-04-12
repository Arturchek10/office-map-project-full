import { forwardRef } from "react"
import { Button } from "@mui/material"

interface AddingFurnitureButtonProps {
  onClick: () => void
}

const AddingFurnitureButton = forwardRef<HTMLButtonElement, AddingFurnitureButtonProps>(
  function AddingFurnitureButton({ onClick }, ref) {
    return (
      <Button
        ref={ref}
        variant="contained"
        onClick={onClick}
      >
        Добавить мебель
      </Button>
    )
  }
)

export default AddingFurnitureButton
