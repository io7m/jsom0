#version 110

uniform mat4 matrix_projection;
uniform mat4 matrix_model;
uniform mat4 matrix_view;
uniform mat3 matrix_normal;
uniform vec3 light_position;

attribute vec3 vertex_position;
attribute vec3 vertex_normal;
attribute vec2 vertex_uv;

varying vec3 f_normal;
varying vec2 f_uv;
varying vec3 f_position_VS;
varying vec3 f_light_direction_VS;

void
main (void)
{
  mat4 mmv  = matrix_view * matrix_model;
  mat4 mmvp = matrix_projection * mmv;

  vec4 position_MS = vec4(vertex_position, 1.0);
  vec4 position_WS = matrix_model * position_MS;
  vec4 position_VS = mmv * position_MS;
  vec4 position_CS = mmvp * position_MS;

  // Light position in view space
  vec4 light_position_WS = vec4(light_position, 1.0);
  vec3 light_position_VS = (mmv * light_position_WS).xyz;

  // Vector from vertex to camera, in view space
  vec3 eye_direction_VS = vec3(0, 0, 0) - position_VS.xyz;

  // Vector from vertex to light, in view space
  vec3 light_direction_VS = light_position_VS + eye_direction_VS;

  gl_Position = position_CS;

  f_light_direction_VS = light_direction_VS;
  f_position_VS        = position_VS.xyz;
  f_normal             = matrix_normal * vertex_normal;
  f_uv                 = vertex_uv;
}
