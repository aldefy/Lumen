# Lumen Coachmark Library
# Minimal ProGuard rules - library uses standard Compose APIs

# Keep sealed class hierarchy for state management
-keep class io.luminos.CoachmarkState { *; }
-keep class io.luminos.CoachmarkState$* { *; }

# Keep sealed class hierarchy for shapes (needed for when expressions)
-keep class io.luminos.CutoutShape { *; }
-keep class io.luminos.CutoutShape$* { *; }

# Keep enum values (used in configs)
-keepclassmembers enum io.luminos.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
