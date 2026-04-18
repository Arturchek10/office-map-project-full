import { useState, useEffect } from "react";
import Drawer from "@mui/material/Drawer";
import TextField from "@mui/material/TextField";
import FormGroup from "@mui/material/FormGroup";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";
import Button from "@mui/material/Button";
import CheckIcon from "@mui/icons-material/Check";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import FormControl from "@mui/material/FormControl";
import FormHelperText from "@mui/material/FormHelperText";
import Input from "@mui/material/Input";
import InputLabel from "@mui/material/InputLabel";
import type {
  MarkerResponse,
  MarkerTypes,
  workspaceMarker,
} from "../../shared/types/marker";
import { updateMarkerFx } from "@shared/store/markers";

type MarkerType = "место" | "комната" | "безопасность" | "утилиты";

type RedactorMenuProps = {
  isOpen: boolean;
  onClose: () => void;
  selectedMarker: MarkerResponse | null;
  onUpdate: (updatedMarker: MarkerResponse) => void;
};

export default function RedactorMenu({
  isOpen,
  onClose,
  selectedMarker,
  onUpdate,
}: RedactorMenuProps) {
  const [selectedType, setSelectedType] = useState<MarkerType>("место");
  const [name, setName] = useState("");
  const [deskAvailable, setDeskAvailable] = useState(false);
  const [uncomfortableDesk, setUncomfortableDesk] = useState(false);
  const [capacity, setCapacity] = useState<number>(0); // для комнаты
  const [text, setText] = useState("");

  // ошибки валидации
  const [nameError, setNameError] = useState("");
  const [uncomfortableError, setUncomfortableError] = useState("");

  const markerTypes: MarkerType[] = [
    "место",
    "комната",
    "безопасность",
    "утилиты",
  ];

  const getCorrectType = (marker: MarkerResponse | null): MarkerType => {
    if (!marker) return "место";
    switch (marker.type) {
      case "workspace":
        return "место";
      case "room":
        return "комната";
      case "emergency":
        return "безопасность";
      case "utility":
        return "утилиты";
      default:
        return "место";
    }
  };

  const ruToApiType = (ruType: MarkerType): MarkerTypes => {
    switch (ruType) {
      case "место":
        return "workspace";
      case "комната":
        return "room";
      case "безопасность":
        return "emergency";
      case "утилиты":
        return "utility";
      default:
        return "workspace";
    }
  };

  const isWorkspaceMarker = (
    marker: MarkerResponse | null
  ): marker is workspaceMarker => {
    return !!marker && marker.type === "workspace";
  };

  useEffect(() => {
    if (!selectedMarker) return;

    setSelectedType(getCorrectType(selectedMarker));

    const payload = selectedMarker.payload || {};

    setDeskAvailable(Boolean(payload.haveComputer || payload.capacity));
    setName(payload.text || "");
    setCapacity(payload.capacity || 0);

    setUncomfortableDesk(
      isWorkspaceMarker(selectedMarker)
        ? Boolean(selectedMarker.uncomfortable)
        : false
    );
  }, [selectedMarker]);

  const saveAttributes = async () => {
    if (!selectedMarker) return;

    // сбрасываем ошибки
    setNameError("");
    setUncomfortableError("");

    let hasError = false;

    // проверка name
    if (!name.trim()) {
      setNameError("Название обязательно для заполнения");
      hasError = true;
    }

    // проверка uncomfortable только для workspace
    if (selectedType === "место" && !uncomfortableDesk) {
      setUncomfortableDesk(false);
      hasError = false;
    }

    if (hasError) return; // не отправляем запрос, если есть ошибки

    const payload: Record<string, any> = { text: name };

    if (selectedType === "место") {
      payload.haveComputer = deskAvailable;
    } else if (selectedType === "комната") {
      payload.capacity = capacity;
    }

    try {
      const updatedMarker = await updateMarkerFx({
        markerId: selectedMarker.id,
        name,
        type: ruToApiType(selectedType),
        payload,
        uncomfortable: isWorkspaceMarker(selectedMarker)
          ? uncomfortableDesk
          : undefined,
      });

      onUpdate(updatedMarker);
      onClose();
    } catch (err) {
      console.error("Ошибка при обновлении маркера", err);
    }
  };

  return (
    <Drawer
      anchor="right"
      open={isOpen}
      onClose={onClose}
      sx={{
        "& .MuiDrawer-paper": { width: 350, padding: 2, marginTop: "60px" },
      }}
    >
      <div className="flex justify-between items-center mb-4">
        <Button variant="text" startIcon={<ArrowBackIcon />} onClick={onClose}>
          назад
        </Button>
        <Button
          variant="contained"
          sx={{ background: "#2F80ED" }}
          className="flex gap-2"
          onClick={saveAttributes}
        >
          <p className="mt-0.5">сохранить</p>
          <CheckIcon sx={{ color: "white" }} />
        </Button>
      </div>

      {/* Тип */}
      <p className="mt-3 mb-5">
        Тип маркера:{" "}
        <b className="text-[#2F80ED] text-xl">{selectedType}</b>
      </p>

      {/* Название */}
      <FormControl variant="standard" sx={{ mb: 5 }} error={!!nameError}>
        <InputLabel htmlFor="marker-name">Название</InputLabel>
        <Input
          id="marker-name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        <FormHelperText>{nameError || "Введите название маркера"}</FormHelperText>
      </FormControl>

      {selectedType === "место" && (
        <FormGroup>
          <FormControlLabel
            control={
              <Checkbox
                checked={deskAvailable}
                onChange={(e) => setDeskAvailable(e.target.checked)}
              />
            }
            label="Есть рабочий стол"
          />
          <FormControlLabel
            control={
              <Checkbox
                checked={uncomfortableDesk}
                onChange={(e) => setUncomfortableDesk(e.target.checked)}
              />
            }
            label="Некомфортное место"
          />
          {uncomfortableError && (
            <FormHelperText error>{uncomfortableError}</FormHelperText>
          )}
        </FormGroup>
      )}

      {selectedType === "комната" && (
        <FormGroup>
          <TextField
            label="Количество рабочих мест"
            type="number"
            value={capacity}
            onChange={(e) => setCapacity(Number(e.target.value) >= 0 ? Number(e.target.value) : 0)}
            sx={{ mb: 1 }}
          />
        </FormGroup>
      )}
    </Drawer>
  );
}
