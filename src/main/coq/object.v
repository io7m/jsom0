(** The type of strings, containing arbitrary UTF-8. *)
Axiom utf8_string : Set.

(** The type of real numbers, axiomatized here. *)
Axiom real : Set.

(** The type of positive integers. *)
Record positive := {
  value       : nat;
  value_range : value >= 0
}.

(** The type of three-dimensional vectors of real numbers. *)
Record vector3f := {
  v3x : real;
  v3y : real;
  v3z : real
}.

(** The type of two-dimensional vectors of real numbers. *)
Record vector2f := {
  v2x : real;
  v2y : real
}.

(** The type of vertices containing a 3D position and normal vector. *)
Inductive vertex_p3n3 := {
  vp3n3_position : vector3f;
  vp3n3_normal   : vector3f
}.

(** The type of vertices containing a 3D position and normal vector, and 2D UV coordinates. *)
Inductive vertex_p3n3t2 := {
  vp3n3t2_position : vector3f;
  vp3n3t2_normal   : vector3f;
  vp3n3t2_uv       : vector2f
}.

(** The type of arrays of length [n], holding elements of type [t]. *)
Axiom array : forall (n : positive) (t : Set), Set.

(** The type of triangles consisting of three indices into an array of vertices. *)
Record triangle := {
  v0 : nat;
  v1 : nat;
  v2 : nat
}.

(** A value of either type [A] or [B], but not both. *)
Inductive either (A B : Set) :=
  | left  : A -> either A B
  | right : B -> either A B.

(** A parsed object. *)
Record object := {
  name          : utf8_string;
  material_name : utf8_string;
  vertices      : forall n, either (array n vertex_p3n3) (array n vertex_p3n3t2);
  triangles     : forall n, array n triangle
}.
