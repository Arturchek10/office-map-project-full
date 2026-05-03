import {
  Dialog,
  DialogTitle,
  DialogContent,
  Stack,
  Typography,
  TextField,
  DialogActions,
  Button,
  Alert,
  Fade,
} from "@mui/material";

import type { MarkerResponse } from "../../shared/types/marker";
import { useState } from "react";

type BookingMarkerFormProps = {
  isOpen: boolean;
  onClose: () => void;
  selectedMarker: MarkerResponse | null;
  onBookingCreated: () => void;
};

export default function BookingMarkerForm({
  isOpen,
  onClose,
  selectedMarker,
  onBookingCreated,
}: BookingMarkerFormProps) {
  const [date, setDate] = useState("");
  const [endTime, setEndTime] = useState("");
  const [startTime, setStartTime] = useState("");

  const [errorMessage, setErrorMessage] = useState("");

  const handleSubmit = async () => {
    console.log("отправка брони на бэк");
    setErrorMessage("");

    // если маркер не выбран  - ничего не отправляем
    if (!selectedMarker) return;

    if (!date || !startTime || !endTime) {
      alert("Выбери дату, время начала и время конца");
      return;
    }
    // формат для backend: 2026-05-03T10:00:00
    const startDateTime = `${date}T${startTime}:00`;
    const endDateTime = `${date}T${endTime}:00`;

    try {
      const response = await fetch("/api/v1/bookings", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          markerId: selectedMarker.id,
          startTime: startDateTime,
          endTime: endDateTime,
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Ошибка бронирования");
      }

      // обновить карту / брони после создания
      onBookingCreated?.();
      onClose();
    } catch (e) {
      if (e instanceof Error) {
        setErrorMessage(e.message);
      } else {
        setErrorMessage("Ошибка бронирования");
      }
      console.error(e);
    }
  };

  const handleClose = async () => {
    setErrorMessage("");
    onClose();
  };
  return (
    <Dialog open={isOpen} onClose={onClose} fullWidth maxWidth="sm">
      {errorMessage && (
        <Fade timeout={300} in={!!errorMessage}>
          <Alert severity="error">{errorMessage}</Alert>
        </Fade>
      )}

      <DialogTitle>Бронирование рабочего места</DialogTitle>

      <DialogContent>
        <Stack spacing={2} sx={{ mt: 1 }}>
          <Typography>Место: {selectedMarker?.id}</Typography>

          <TextField
            label="Дата"
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            fullWidth
          />

          <TextField
            label="Время начала"
            type="time"
            value={startTime}
            onChange={(e) => setStartTime(e.target.value)}
            fullWidth
          />

          <TextField
            label="Время окончания"
            type="time"
            value={endTime}
            onChange={(e) => setEndTime(e.target.value)}
            fullWidth
          />
        </Stack>
      </DialogContent>

      <DialogActions>
        <Button onClick={handleClose}>Отмена</Button>

        <Button variant="contained" onClick={handleSubmit}>
          Забронировать
        </Button>
      </DialogActions>
    </Dialog>
  );
}
