#version 110

uniform mat4 matrix_projection;
uniform mat4 matrix_modelview;
uniform mat3 matrix_normal;

attribute vec3 vertex_position;
attribute vec3 vertex_normal;
attribute vec2 vertex_uv;

varying vec3 vertex_position;
varying vec3 vertex_normal;
varying vec2 vertex_uv;

void
main (void)
{
  vertex_uv       = uv;
  vertex_normal   = matrix_normal * normal;

  vec4 p          = matrix_projection * matrix_modelview * vec4(position, 1.0);
  gl_Position     = p;
  vertex_position = p.xyz;
}
