#version 130

uniform sampler2D t_diffuse_0;

in vec3 f_normal;

out vec4 out_frag_color;

void
main (void)
{
  out_frag_color = texture2D(t_diffuse_0, f_normal.xy);
}
