#version 130

out vec4 out_frag_color;

in vec3 f_color;

void
main (void)
{
  out_frag_color = vec4(f_color, 1.0);
}
