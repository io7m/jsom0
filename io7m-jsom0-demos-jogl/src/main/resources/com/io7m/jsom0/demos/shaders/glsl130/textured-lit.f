#version 130

uniform sampler2D t_diffuse_0;

uniform vec3 l_color;
uniform float l_intensity;
uniform vec4 l_direction;

in vec4 f_position;
in vec2 f_uv;
in vec3 f_normal;

out vec4 out_frag_color;

void
main (void)
{
  vec4 V = normalize (f_position);
  vec4 N = normalize (vec4 (f_normal, 0.0));
  vec4 L = -l_direction;
  vec4 R = reflect (V, N);

  float l_diffuse_factor = max (0, dot (L, N));
  vec4 l_diffuse_color   = vec4 (l_color, 1.0) * l_intensity * l_diffuse_factor;

  float l_spec_factor = pow (max (dot (R, L), 0.0), 32);
  vec4 l_spec_color   = vec4 (l_color, 1.0) * l_intensity * l_spec_factor;

  out_frag_color = texture2D(t_diffuse_0, f_uv) * l_diffuse_color + l_spec_color;
}
