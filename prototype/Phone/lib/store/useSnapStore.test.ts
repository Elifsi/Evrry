import { describe, it, expect, beforeEach } from "vitest";
import { useSnapStore } from "./useSnapStore";

describe("useSnapStore - Memories & My Locker", () => {
  beforeEach(() => {
    // Reset state before each test
    useSnapStore.setState({
      snaps: [],
      cameraRoll: [],
      lockerSnaps: [],
      lockerPin: "1234",
      isLockerUnlocked: false,
    });
  });

  it("adds and deletes a snap in Memories", () => {
    const snap = useSnapStore.getState().addSnap("data:image/jpeg;base64,123", "Kathmandu Gold", "Patan", "Lovely sunset");
    expect(useSnapStore.getState().snaps).toHaveLength(1);
    expect(useSnapStore.getState().snaps[0].id).toBe(snap.id);
    expect(useSnapStore.getState().snaps[0].location).toBe("Patan");

    useSnapStore.getState().deleteSnap(snap.id);
    expect(useSnapStore.getState().snaps).toHaveLength(0);
  });

  it("imports and removes camera roll photos", () => {
    const item = useSnapStore.getState().addToCameraRoll("data:image/jpeg;base64,456", "Vacation photo");
    expect(useSnapStore.getState().cameraRoll).toHaveLength(1);
    expect(useSnapStore.getState().cameraRoll[0].caption).toBe("Vacation photo");

    useSnapStore.getState().deleteCameraRoll(item.id);
    expect(useSnapStore.getState().cameraRoll).toHaveLength(0);
  });

  it("moves a photo to My Locker and protects it behind PIN", () => {
    const snap = useSnapStore.getState().addSnap("data:image/jpeg;base64,secret", "Secret Filter");
    expect(useSnapStore.getState().snaps).toHaveLength(1);

    // Move to My Locker
    useSnapStore.getState().moveToLocker(snap.id, "snaps");
    expect(useSnapStore.getState().snaps).toHaveLength(0);
    expect(useSnapStore.getState().lockerSnaps).toHaveLength(1);
    expect(useSnapStore.getState().lockerSnaps[0].isLocked).toBe(true);

    // Initial state is locked
    expect(useSnapStore.getState().isLockerUnlocked).toBe(false);

    // Wrong PIN fails unlock
    const wrong = useSnapStore.getState().unlockLocker("0000");
    expect(wrong).toBe(false);
    expect(useSnapStore.getState().isLockerUnlocked).toBe(false);

    // Correct PIN succeeds unlock
    const right = useSnapStore.getState().unlockLocker("1234");
    expect(right).toBe(true);
    expect(useSnapStore.getState().isLockerUnlocked).toBe(true);

    // Locking vault resets state
    useSnapStore.getState().lockLocker();
    expect(useSnapStore.getState().isLockerUnlocked).toBe(false);
  });

  it("restores a locked photo back to Snaps", () => {
    const snap = useSnapStore.getState().addSnap("data:image/jpeg;base64,pic", "Vintage");
    useSnapStore.getState().moveToLocker(snap.id, "snaps");
    expect(useSnapStore.getState().snaps).toHaveLength(0);

    const lockedId = useSnapStore.getState().lockerSnaps[0].id;
    useSnapStore.getState().restoreFromLocker(lockedId);
    expect(useSnapStore.getState().lockerSnaps).toHaveLength(0);
    expect(useSnapStore.getState().snaps).toHaveLength(1);
    expect(useSnapStore.getState().snaps[0].isLocked).toBe(false);
  });
});
