#version 130

in vec3 v_position;
in vec3 v_normal;
in vec2 v_uv;
in vec3 v_color;

uniform mat4 m_modelview;
uniform mat4 m_projection;

out vec4 f_position;
out vec2 f_uv;
out vec3 f_normal;
out vec3 f_normal_raw;
out vec3 f_color;

void
main()
{
  f_position   = m_modelview * vec4(v_position, 1.0);
  f_uv         = v_uv;
  f_normal     = (m_modelview * vec4(v_normal, 0.0)).xyz;
  f_normal_raw = v_normal;
  f_color      = v_color;

  gl_Position  = m_projection * f_position;
}
