(** The type of strings, containing arbitrary UTF-8. *)
Axiom utf8_string : Set.

(** The type of real numbers, axiomatized here. *)
Axiom real : Set.

(** The type of real numbers in the range [0.0, 1.0]. *)
Axiom real_clamp : Set.

(** The type of RGB vectors. *)
Record rgb := {
  rgb_r : real_clamp;
  rgb_g : real_clamp;
  rgb_b : real_clamp
}.

(** The type of RGBA vectors. *)
Record rgba := {
  rgba_r : real_clamp;
  rgba_g : real_clamp;
  rgba_b : real_clamp;
  rgba_a : real_clamp
}.

(** The type of texture mapping. *)
Inductive mapping :=
  | Map_Chrome
  | Map_UV.

(** The type of textures. *)
Record texture := {
  t_name    : utf8_string;
  t_alpha   : real_clamp;
  t_mapping : mapping
}.

(** A parsed material. *)
Record material := {
  m_name      : utf8_string;
  m_diffuse   : rgb;
  m_ambient   : rgb;
  m_specular  : rgba;
  m_shininess : real;
  m_alpha     : real_clamp;
  m_texture   : option texture
}.
