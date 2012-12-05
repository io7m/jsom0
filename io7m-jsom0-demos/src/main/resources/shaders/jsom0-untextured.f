#version 110

varying vec3 f_normal;
varying vec2 f_uv;
varying vec3 f_position_WS;
varying vec3 f_eye_direction_VS;
varying vec3 f_light_direction_VS;

uniform vec3      light_position;
uniform vec3      light_color;
uniform float     light_power;
uniform vec3      material_diffuse;
uniform vec3      material_ambient;
uniform vec3      material_specular;

void
main (void)
{
  float distance    = length(light_position - f_position_WS);
  float distance_sq = distance * distance;
  vec3 reflection   = reflect(-f_light_direction_VS, f_normal);

  float cos_theta  = clamp(dot(f_normal,           f_light_direction_VS), 0, 1);
  float cos_alpha  = clamp(dot(f_eye_direction_VS, reflection),           0, 1);

  vec3 result = vec3(0,0,0);
  result      = result + (material_diffuse * (light_color * (light_power * cos_theta / distance_sq)));

  gl_FragColor = vec4(result, 1.0);
}