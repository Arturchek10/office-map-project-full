import React, { useRef, useState, useEffect } from "react"
import { Image as KonvaImage, Transformer } from "react-konva"
import Konva from "konva"
import useImage from "use-image"

interface FurnitureOnMapProps {
  id: number
  photo: string
  x: number
  y: number
  width?: number
  height?: number
  onSizeChange?: (width: number, height: number) => void
  initialWidth?: number
  initialHeight?: number
  rotation?: number
  onRotationChange?: (angle: number) => void
  onPositionChange?: (x: number, y: number) => void
  editable?: boolean
  onContextMenu?: (id: number, pos: { x: number; y: number }) => void
}

const snapToGrid = (value: number, gridSize = 3) =>
  Math.round(value / gridSize) * gridSize

const FurnitureOnMap: React.FC<FurnitureOnMapProps> = ({
  id,
  photo,
  x,
  y,
  width = 100,
  height = 100,
  onSizeChange,
  onRotationChange,
  onPositionChange,
  rotation: initialRotation = 0,
  initialWidth,
  initialHeight,
  editable = true,
  onContextMenu,
}) => {
  const [img] = useImage(photo)
  const [size, setSize] = useState({
    width: initialWidth || width,
    height: initialHeight || height,
  })
  const [rotation, setRotation] = useState(initialRotation)

  const imageRef = useRef<Konva.Image>(null)
  const transformerRef = useRef<Konva.Transformer>(null)

  // Синхронизируем позицию изображения с пропсами при их изменении
  useEffect(() => {
    if (imageRef.current) {
      imageRef.current.x(x)
      imageRef.current.y(y)
      imageRef.current.getLayer()?.batchDraw()
    }
  }, [x, y])

  useEffect(() => {
    if (editable && transformerRef.current && imageRef.current) {
      transformerRef.current.nodes([imageRef.current])
      transformerRef.current.getLayer()?.batchDraw()
    }
  }, [img, editable])

  if (!img) return null

  return (
    <>
      <KonvaImage
        ref={imageRef}
        image={img}
        x={x}
        y={y}
        width={size.width}
        height={size.height}
        rotation={rotation}
        draggable={editable}
        onContextMenu={(e) => {
          e.evt.preventDefault()
          e.evt.stopPropagation()
          e.evt.stopImmediatePropagation()

          if (editable && onContextMenu) {
            console.log("Context menu opened for furniture:", id)
            onContextMenu(id, { x: e.evt.clientX, y: e.evt.clientY })
          }
        }}
        onDragMove={(e) => {
          if (!editable) return
          const snappedX = snapToGrid(e.target.x())
          const snappedY = snapToGrid(e.target.y())
          e.target.x(snappedX)
          e.target.y(snappedY)
        }}
        onDragEnd={(e) => {
          if (!editable) return
          const newX = snapToGrid(e.target.x())
          const newY = snapToGrid(e.target.y())

          if (onPositionChange) {
            onPositionChange(newX, newY)
          }
        }}
        onTransformEnd={() => {
          if (!editable || !imageRef.current) return
          const node = imageRef.current

          let newWidth = node.width() * node.scaleX()
          let newHeight = node.height() * node.scaleY()

          newWidth = snapToGrid(newWidth)
          newHeight = snapToGrid(newHeight)

          let newRotation = node.rotation()
          newRotation = Math.round(newRotation / 15) * 15

          setSize({ width: newWidth, height: newHeight })
          setRotation(newRotation)

          node.rotation(newRotation)
          node.scaleX(1)
          node.scaleY(1)

          if (onRotationChange) onRotationChange(newRotation)
          if (onSizeChange) onSizeChange(newWidth, newHeight)
        }}
      />
      {editable && (
        <Transformer
          ref={transformerRef}
          rotateEnabled={true}
          enabledAnchors={[
            "top-left",
            "top-right",
            "bottom-left",
            "bottom-right",
          ]}
          anchorStroke="#56CCF2"
          anchorFill="#56CCF2"
          borderStroke="#56CCF2"
          borderDash={[10, 5]}
        />
      )}
    </>
  )
}

export default FurnitureOnMap
